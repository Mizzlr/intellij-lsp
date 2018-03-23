set -x

rm -rf intellij-lsp/target
rm -rf project/project/target
rm -rf project/target
rm -rf target
rm -rf intellij-lsp/build

cd intellij-lsp
gradle fatJar
rm -rf tmp
mkdir tmp

cd tmp
jar -xf ../build/libs/LSP-fat.jar

cd ../..
sbt package

cd intellij-lsp/tmp
jar -xf ../target/scala-2.12/intellij-language-server_2.12-1.0.0.jar
echo "Manifest-Version: 1.0
Implementation-Title: intellij-language-server
Implementation-Version: 1.0.0
Specification-Vendor: intellij-language-server
Specification-Title: intellij-language-server
Implementation-Vendor-Id: intellij-language-server
Specification-Version: 1.0.0
Implementation-Vendor: intellij-language-server" > META-INF/MANIFEST.MF

cd ../..
jar -cf Intellij-Language-Server.jar -C intellij-lsp/tmp .
rm -rf intellij-lsp/tmp
