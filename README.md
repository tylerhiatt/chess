# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared tests`     | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

### Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
### [Sequence Diagram Link](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWOZVYSnfoccKQCLAwwAIIgQKAM4TMAE0HAARsAkoYMhZkzowUAJ4TcRNAHMYABgB0ADkzGoEAK7YYAYjTAqumACUUxpBI6gkgQaK4A7gAWSGAciKikALQAfCzUlABcMADaAAoA8mQAKgC6MAD0DipQADpoAN5VlO4AtigANDC4UuHQMp0oLcBICAC+mBTpsClpbOJZUH4BsVAAFE1QrR1dyhK9UP0wg8MIAJQTrOwwMwJCouJSWcYoYACq1evVW+e3ImKSEmuqXUWTIAFEADJguBFGAbLYwABm9hacOqmF+9wB1xmkzmKCyaAcCAQF2oVxu8ixjxgIEWghQ70on2awDanW6ez6Pyp-ykQLUCiyAEkAHLg7yw+Fs7ac-aHY4jGBior5NG0PEU1KYvkSLJ0lAM4QOMCRFmbGU8u66gUg5XisGS9UW9kwYAmyJFCAAa3Q9tVbo9GN5D0BiVxl3mgdNXt9aDJ+JxqTxmWjnp96ATlCT8GQ2iyACYzGY6vV3TGM2hxugZJo7I5nC5oLxnjAIRB-GEXFEYnE80lk6xU3lCqUKiopCE0KXpa65X1xinpoPyVGEB2kGhzVsObt5edNeIgTrQ08Xky1rOUFa-qHbULyJDoVKvjKkSjnQn2ClDwSYFRgGQORYgvbdLS-I9KWtU9aXpWJjVNMC2hvakw2BB8xQlF9WVdct0zjf01TwiDVCg28ATPMAELNK8UJtGY7XBKEYTTWM-QAMW8fIAFk0xgAB1AAJR0wWdBEAF4YHqAwUCIRFCAAfSvcYT2xcMV3xLI8LY+NfxzJctI9HSs2mGZ4nzGAixLBptMras0FrWx7CcVw7BQP122MRxmG7aJYkwcyB1mYc+CfIowTKcoJwkKc6lsuMTKBX8MnXLyTVWeL0APSNSOSVSaRkFAEBeFBqIyozKzou8GIfUKoXCmBOJ4vihJE1jKxgSTpOwWSkHkkAFMgOMVJDNSfxyrIJAcXUSP0ocoEm6bQ0S9TArAQtiy0By62cxtFhkNtlhgABxGVAV83sAv7ZgIymLJsmOsEx3KYwZTiiqEqXAVkpgZBAmEElTraCRVmy1dcpgfK9RgZ4qI9cqKzjKq1PQ0EnxYq9Gq43i8IE4TvFEzKwi6mS5MUob0BG6Cxo09gsivWayNQrI-rAIHJFB4Nqf5Gq0eY2FXraYU+E6HsQNfV0FAQUBvQvHcYZlUU3ya3jBY4KG5vBp4zohZZGdumgFoV4HdcCFazOujbrPqY3JHuqTbeF0W-MZCXtilmW5ZlTo1aVtoYFGGAynsxz6xclxsAcKBsGK+A4NUdmIhdq6EhujThwKYpIrV97EfQUtfZlRd5u+ibYMNWJRRQcJ2dWQvXVoxntVGmlYbKonkZ51HH35sTlexlr8cJj6-RJnqyYGimqy58ieYNum+7aJvIZb6GDQZWv6+2RuNd5+0sMI22YDrxXvZgMXXZw68YAANWECFXjBMhj63zod9XzXNMdvh9fTo21eFubVIa0ralm-gHLaoddquF0EVdc4QYAACkICbhOm+FwHsQDehTtoUud0ci5FeM9HO7gR7TgaGtOAEB1xQB9jKYWxcph4K-gAKxQWgTe9CRaL1lNLWBUAqE0LBomJmupKLtzIZ3NCgo+bPh4VjZquNWoE3agRMevV+oKSJlTWeYZ55RgZnpURMFYacKFnwKR95ZEsQAFQKNVm+ZRokAF8E6lJUmfVFIuJ0ahT+C81Z8BeCcaQRjm7c2hk4YCKB2a5D4cyFxb83YcjiQI6h0BLF71eLkPgwgGpq0fLCC+Xt-bqInkpN2gd8jeBgJg2Wbs3HdQ0YpZSeM2ouIaR4zR3iZ6+PUrMBeU0ZpGINqmQZy0vp9JAZZTaNYdoNlclAYAXhECGlgMAbAMdCDBFCEnS6a1mEhTChFCo6gVq0yjCAYqeBfhKBUJzUJK9wn6iuVAC8IMMndzqk9USKtPy730X+MZAJl5Q2eas9m7yen0U+Uc+xtsoXVQBYtIZOUBSgtpC86iOlIW7xhfVH5A9iL-POYCpawLhl-2ReMkukzLbTLMJAzAQA)