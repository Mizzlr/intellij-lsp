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
jar -xf ../target/scala-2.12/df-intellij-lsp_2.12-1.0.0.jar
echo "Manifest-Version: 1.0
Implementation-Title: df-intellij-lsp
Implementation-Version: 1.0.0
Specification-Vendor: df-intellij-lsp
Specification-Title: df-intellij-lsp
Implementation-Vendor-Id: df-intellij-lsp
Specification-Version: 1.0.0
Implementation-Vendor: df-intellij-lsp" > META-INF/MANIFEST.MF

cd ../..
jar -cf Intellij-Language-Server.jar -C intellij-lsp/tmp .
rm -rf intellij-lsp/tmp
