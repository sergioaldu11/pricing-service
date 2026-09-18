# Inditex Pricing Service

Servicio de precios implementado con arquitectura hexagonal en un único módulo Maven. Resuelve `GET /api/prices` seleccionando el precio vigente por marca, producto, fecha y prioridad.

## Stack y requisitos

- Java 27
- Spring Boot 4.1.1 / Spring Framework 7
- H2 + Liquibase
- JUnit 6, Mockito, ArchUnit, JaCoCo y PIT

Se incluye Maven Wrapper. Con JDK 27 configurado:

```powershell
.\mvnw.cmd clean verify
.\mvnw.cmd clean verify -Pmutation
```

El segundo comando añade análisis mutacional y genera los informes de calidad. Para ejecutar el servicio:

```powershell
java -jar target/pricing-service-1.0.0-SNAPSHOT.jar
```

La aplicación usa H2 en memoria y carga los datos del enunciado mediante Liquibase.

## API

`GET /api/v1/prices?applicationDate=2020-06-14T16:00:00&productId=35455&brandId=1`

La fecha se interpreta como `LocalDateTime` ISO sin zona. La vigencia es inclusiva; se elige la mayor prioridad y, en empate, el inicio más reciente y el identificador más bajo como desempate determinista.

Las respuestas de error siguen RFC 9457 (`application/problem+json`) e incluyen códigos estables como `PRICE_NOT_FOUND`, `HTTP_400`, `HTTP_405` e `INTERNAL_ERROR`. Los errores inesperados generan además un `errorId` y la cabecera `X-Error-Id` para correlación sin exponer detalles internos.

En producción se activa `PRICING_SECURITY_ENABLED=true` y se configura `PRICING_SECURITY_ISSUER_URI` junto con `PRICING_SECURITY_AUDIENCE`. El endpoint exige un JWT Bearer con el scope `pricing.read`; `/actuator/health` queda disponible para las comprobaciones de la plataforma. En local la seguridad permanece desactivada para facilitar la ejecución de la prueba.

## Estructura hexagonal

- `domain`: value objects, entidad y reglas de negocio sin dependencias de Spring ni de infraestructura.
- `application`: casos de uso y puertos que pertenecen al núcleo.
- `adapters.in`: REST, DTOs, conversión de fechas y manejo de errores.
- `adapters.out`: persistencia JPA y mapeadores de infraestructura.
- `config`: composición de dependencias y arranque de Spring Boot.

El proyecto es un único artefacto Maven; la separación se mantiene por paquetes y reglas ArchUnit. Las dependencias apuntan hacia dentro: el dominio solo usa JDK, la aplicación solo dominio/JDK y los adaptadores implementan los puertos del núcleo.

## Estrategia de pruebas

- Unitarias del dominio y aplicación, incluyendo constructores, límites, validación y casos generados.
- REST con MockMvc para contratos, validación, errores RFC 9457 y mapeos.
- Integración con H2 + Liquibase para prioridad, intervalos inclusivos, solapes, desempates y restricciones SQL.
- Prueba de arquitectura con ArchUnit.
- Smoke test del JAR empaquetado mediante HTTP real.
- JaCoCo con gates de líneas y ramas; PIT con umbral mutacional del 95% (adaptadores alcanzan 97% en la ejecución validada).

Las decisiones de estructura están documentadas en [`docs/architecture.md`](docs/architecture.md); los detalles de ejecución y los informes, en [`docs/testing.md`](docs/testing.md). El contrato OpenAPI está en [`docs/openapi.yaml`](docs/openapi.yaml).

## CI

`.github/workflows/verify.yml` ejecuta `clean verify -Pmutation` en Linux y Windows, publica los informes de cobertura y mutación y comprueba el formato con Spotless.
