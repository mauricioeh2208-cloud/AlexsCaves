# Alex's Caves a Fabric 1.21.1

Este directorio es un hito inicial del port. No reemplaza la build Forge actual del repo; crea una base aislada para empezar a migrar a Fabric 1.21.1 sin romper el proyecto original.

## Estado medido del codigo original

- Base actual: Forge 1.20.1 con ForgeGradle 5, Parchment y mixins.
- Archivos Java con acoplamiento directo a Forge: 237.
- Archivos Java con dependencias directas de Citadel: 198.
- Recursos con namespaces o condiciones especificas de Forge o Citadel: tags `forge:`, loot modifiers, `mods.toml`, `data/forge`, avances con items `citadel:*`.

## Que incluye este scaffold

- Proyecto Gradle independiente en `fabric-port/` con Loom para Minecraft 1.21.1.
- Wrapper dedicado de Gradle para no tocar la toolchain Forge del repo raiz.
- `ModInitializer` minimo para arrancar en Fabric.
- `fabric.mod.json` y archivo base de mixins.
- Script `tools/scan-port-blockers.ps1` para volver a medir bloqueos del port mientras se migra codigo.

## Orden recomendado para seguir

1. Portar bootstrap y registries.
   La clase `AlexsCaves` depende de `@Mod`, `FMLJavaModLoadingContext`, `MinecraftForge.EVENT_BUS`, `ForgeConfigSpec`, `SimpleChannel` y `DeferredRegister`.
2. Sustituir infraestructura de loader.
   Networking, config, event bus, chunk loading, spawn hooks, capabilities e item handlers requieren reemplazo por APIs Fabric o una capa `platform`.
3. Separar dependencias externas.
   Citadel es una dependencia estructural del mod, no solo visual. Antes de mover mobs, modelos y renderers hay que decidir si se porta Citadel, se reemplaza por otra libreria o se implementa una capa local.
4. Limpiar recursos dependientes de Forge.
   Loot modifiers, condiciones `forge:*`, `data/forge/**` y referencias `citadel:*` en advancements no se pueden copiar tal cual al build Fabric.

## Comandos utiles

- Auditar bloqueos: `powershell -ExecutionPolicy Bypass -File .\fabric-port\tools\scan-port-blockers.ps1`
- Compilar scaffold Fabric: `.\fabric-port\gradlew.bat build`

## Alcance actual

Este scaffold compila solo el bootstrap de Fabric. Todavia no intenta reutilizar `src/main/java` del mod original porque eso introduciria cientos de errores de compilacion atados a Forge y Citadel.
