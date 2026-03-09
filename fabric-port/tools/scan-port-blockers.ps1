param(
    [string]$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..\\..")).Path
)

$ErrorActionPreference = "Stop"

if (-not (Get-Command rg -ErrorAction SilentlyContinue)) {
    throw "ripgrep (rg) is required to run this audit."
}

function Get-RgFileCount {
    param(
        [string]$Pattern,
        [string]$Path
    )

    $results = & rg -l -e $Pattern -- $Path 2>$null
    if ($LASTEXITCODE -gt 1) {
        throw "rg failed while scanning $Path"
    }
    return @($results).Count
}

function Get-RgFileList {
    param(
        [string]$Pattern,
        [string]$Path,
        [int]$Limit = 10
    )

    $results = & rg -l -e $Pattern -- $Path 2>$null
    if ($LASTEXITCODE -gt 1) {
        throw "rg failed while scanning $Path"
    }
    return @($results) | Select-Object -First $Limit
}

$javaRoot = Join-Path $RepoRoot "src/main/java"
$resourceRoot = Join-Path $RepoRoot "src/main/resources"

$forgePattern = "net\.minecraftforge|@Mod|ModLoadingContext|FMLJavaModLoadingContext|MinecraftForge|ForgeConfigSpec|NetworkHooks|PlayMessages|ForgeCapabilities|ToolActions|DeferredRegister|RegistryObject"
$citadelPattern = "com\.github\.alexthe666\.citadel"
$jeiPattern = "mezz\.jei"
$forgeResourcePattern = 'forge:'
$citadelResourcePattern = 'citadel:'

$report = [ordered]@{
    "Forge-coupled Java files" = Get-RgFileCount -Pattern $forgePattern -Path $javaRoot
    "Citadel-coupled Java files" = Get-RgFileCount -Pattern $citadelPattern -Path $javaRoot
    "JEI integration Java files" = Get-RgFileCount -Pattern $jeiPattern -Path $javaRoot
    "Resources with forge namespace usage" = Get-RgFileCount -Pattern $forgeResourcePattern -Path $resourceRoot
    "Resources with citadel references" = Get-RgFileCount -Pattern $citadelResourcePattern -Path $resourceRoot
}

Write-Output "AlexsCaves Fabric Port Audit"
Write-Output "Repo: $RepoRoot"
Write-Output ""

foreach ($entry in $report.GetEnumerator()) {
    Write-Output ("{0}: {1}" -f $entry.Key, $entry.Value)
}

Write-Output ""
Write-Output "Sample Forge-coupled files:"
Get-RgFileList -Pattern $forgePattern -Path $javaRoot | ForEach-Object { Write-Output (" - {0}" -f $_) }

Write-Output ""
Write-Output "Sample Citadel-coupled files:"
Get-RgFileList -Pattern $citadelPattern -Path $javaRoot | ForEach-Object { Write-Output (" - {0}" -f $_) }
