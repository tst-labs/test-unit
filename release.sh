#!/bin/bash

echo Qual a versão sendo lançada \(ex.: 1.0.0\)?
read releaseVersion

echo Qual será a próxima versão \(ex.: 1.1.0-SNAPSHOT\)?
read nextVersion

mvn clean install

mvn versions:set -DnewVersion=$releaseVersion

mvn clean install && mvn versions:commit

git add ./\pom.xml
git commit -m "Release da versão $releaseVersion"
git push origin

tagName=test-unit-parent-$releaseVersion-$(date +'%Y%m%d-%H%M')
git tag -a $tagName -m "Release da versão $releaseVersion"
git push origin $tagName

mvn versions:set -DnewVersion=$nextVersion
git add ./\pom.xml
git commit -m "Preparando para a próxima versão: $nextVersion"
git push origin
