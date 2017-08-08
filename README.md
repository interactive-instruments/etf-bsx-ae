# ETF BaseX Adapters & Extensions

Adapter and extension functions for BaseX-based test projects for ETF

[![European Union Public Licence 1.2](https://img.shields.io/badge/license-EUPL%201.2-blue.svg)](https://joinup.ec.europa.eu/software/page/eupl)

&copy; 2010-2017 interactive instruments GmbH. Licensed under the EUPL.

## About ETF

ETF is an open source testing framework for validating spatial data, metadata and web services in Spatial Data Infrastructures (SDIs). For documentation about ETF, see [http://docs.etf-validator.net](http://docs.etf-validator.net/).

Please report issues [in the GitHub issue tracker of the ETF Web Application](https://github.com/interactive-instruments/etf-webapp/issues).

ETF component version numbers comply with the [Semantic Versioning Specification 2.0.0](http://semver.org/spec/v2.0.0.html).

## Build information

Build the library with `./gradlew build`.

You can publish the library by uploading it to the interactive instruments
repository with `./gradlew uploadArchives` (NOTE: the repository does not allow
that existing libraries are overwritten, so make sure to increment the library
version in the version.properties file).

