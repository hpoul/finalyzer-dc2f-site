#!/bin/bash

set -xe

rsync --progress -a public/ web.sphene.net:public_html/finalyzer-dc2f-site/

echo "Synced to http://finalyzer-dc2f-site.codeux.design/"

