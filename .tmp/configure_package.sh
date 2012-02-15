#!/bin/bash

package=$1
package_dir=$(echo src/$package|sed 's/\./\//g')
mkdir -p $package_dir
mv src/com/tudelft/triblerdroid/first/* $package_dir/
rmdir src/com/tudelft/triblerdroid/first
rmdir src/com/tudelft
rmdir src/com
source_files=$package_dir/*
for filename in $source_files AndroidManifest.xml build.properties;
do
	sed 's/com\.tudelft\.triblerdroid\.first/'$package'/g' $filename > tmp; mv tmp $filename;
done
