#!/bin/bash

set -xe

dir="${0%/*}"
basedir="$dir/.."

mkdir -p ~/.ssh
echo "[web.sphene.net]:22031 ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEAui1i7WdYbBDi85BiZT7gzwZlMtF+h7+8ybu8iqWEKzHyh8fnR4iW1JgRRmWK4BCWHrgwY3MCK7Z2bvJWoHn0v0rBaCuRAFhaBlcUsgPKKETu1RceoBD2bS4o2fMqVDMISsZL6hKkRTxK+koLolt8hhjSBKHwxG4nlKa4e2x5d+AmYWEHG/Iy1/7ouCP0YY8t2wjo6r/6tsD/BgVp5b7YNTZ/ZWL4OowBAGelwuxp796dYjbxqtWmOdrZvpKfSzdLw10IEmnVuXQqI6yDg/Y/ZpXzOQ6Orl097ca+n8Zh0teHTq++4s+HOaZJoZYHjiAvwVgHy9cLZsNGYIJHPe897Q==" >> ~/.ssh/known_hosts

chmod 600 $dir/secrets/id_rsa.web.sphene.net

rsync -e "ssh -i $dir/secrets/id_rsa.web.sphene.net -p 22031" --progress -a --delete "$basedir/public/" herbert@web.sphene.net:public_html/finalyzer-dc2f-site-prod/

echo "Synced to http://finalyzer-dc2f-site.codeux.design/"

