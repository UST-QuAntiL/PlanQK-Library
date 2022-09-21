# PlanQK Library

A simple REST API to use [JabRef](https://github.com/JabRef/jabref) as a service.

## Git Workflow

We use the Gitflow-Workflow as our Git-Workflow. A detailed description about the workflow can be found [here](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow).

## Build

1. Run `gradle build` inside the root folder.
2. When completed, the built product can be found in `build/libs/PlanQK Library-1.0-SNAPSHOT.war`.

### Running and Development

The PlanQK Library is built based on [Tomcat in version 10](https://tomcat.apache.org/download-10.cgi).
Thus, earlier version will not be able to host the application since they are not using the Jakarta XML library.

## Acknowledgements

Current development is supported by the [Federal Ministry for Economic Affairs and Climate Action (BMWK)] as part of the [PlanQK] project (01MK20005N).

## Haftungsausschluss

Dies ist ein Forschungsprototyp. Die Haftung für entgangenen Gewinn, Produktionsausfall, Betriebsunterbrechung,
entgangene Nutzungen, Verlust von Daten und Informationen, Finanzierungsaufwendungen sowie sonstige Vermögens- und
Folgeschäden ist, außer in Fällen von grober Fahrlässigkeit, Vorsatz und Personenschäden, ausgeschlossen.

## Disclaimer of Warranty

Unless required by applicable law or agreed to in writing, Licensor provides the Work (and each Contributor provides its
Contributions) on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied, including,
without limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A
PARTICULAR PURPOSE. You are solely responsible for determining the appropriateness of using or redistributing the Work

## License

SPDX-License-Identifier: MIT

   [Federal Ministry for Economic Affairs and Climate Action (BMWK)]: https://www.bmwk.de/EN
   [PlanQK]: https://planqk.de
