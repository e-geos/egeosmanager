#!/bin/bash

wd=/tmp/geoserver_war
mvn_cache=/var/cache/maven
wget -O /tmp/geoserver.zip "http://downloads.sourceforge.net/project/geoserver/GeoServer/2.8.2/geoserver-2.8.2-war.zip?r=http%3A%2F%2Fgeoserver.org%2Frelease%2Fstable%2F&ts=1455615000&use_mirror=netix" 

mvn clean package

rm -fr $wd
mkdir -p $wd
cd $wd

#geoserver.war is the original geoserver application as downloaded 
#no customization is required
unzip /tmp/geoserver.zip geoserver.war
unzip geoserver.war
rm -fr /tmp/geoserver.zip geoserver.war

#insert in WEB-INF/lib of geoserver.war egeosmanager-1.0.jar and egs-utils-1.0.jar
# - egeosmanager-1.1.jar is taken from this project
# - egs-utils-1.0.jar is taken from https://github.com/e-geos/egs-utils

cp $OLDPWD/target/egeosmanager-1.1.jar WEB-INF/lib
cp `find $mvn_cache/it/egeos/geoserver/egs-utils/ -name egs-utils-1.0.jar` WEB-INF/lib

#Dependency of egs-utils-1.0.jar
cp `find $mvn_cache/org/jasypt/jasypt/ -name jasypt-1.9.2.jar` WEB-INF/lib
cp `find $mvn_cache/commons-codec/commons-codec/ -name commons-codec-1.9.jar` WEB-INF/lib
cp `find $mvn_cache/commons-io/commons-io/ -name commons-io-2.4.jar` WEB-INF/lib
cp `find $mvn_cache/org/apache/commons/commons-lang3 -name commons-lang3-3.3.2.jar` WEB-INF/lib
cp `find $mvn_cache/org/apache/commons/commons-collections4/ -name commons-collections4-4.0.jar` WEB-INF/lib
cp `find $mvn_cache/commons-pool/commons-pool/ -name commons-pool-1.5.3.jar` WEB-INF/lib

# Rebuild of war
zip -r $OLDPWD/geoserver.war .

cd -
