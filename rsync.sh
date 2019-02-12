#!/bin/bash

set -xe

rsync -a public/ web.sphene.net:public_html/finalyzer-dc2f-site/

