# mySharesGenerator

- To build: ./gradlew onejarbuildtask
- To run in the build/libs dir: java -jar sharesGenerator.jar

### Docker

- To build: docker build -t generalmeow/mysharesgenerator:<tag> .
- For arm : docker build -t generalmeow/mysharesgenerator:<tag-arm> -f Dockerfile-arm .
- To run: docker run -d -p 5555:5555 --name mysharesgenerator generalmeow/mysharesgenerator<tag>