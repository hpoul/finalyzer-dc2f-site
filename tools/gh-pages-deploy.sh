#!/usr/bin/env bash

set -xe

repodir=`pwd`

pushd "${0%/*}"

tmpdir=tmp

rm -rf "$tmpdir"
mkdir "$tmpdir"

rsa=`pwd`/secrets/github-pages_id_rsa
chmod 600 $rsa

GIT_SSH_COMMAND="ssh -v -i $rsa" git clone -b gh-pages git@github.com:${TRAVIS_REPO_SLUG} "$tmpdir"

# clean directory...
rm -rf "${tmpdir}/*"
# remove all remaining . files (.htaccess), but keep .git sub directory.
#find "${tmpdir}" -type f -exec rm {} \;

cp -a ${repodir}/public/ "${tmpdir}"

pushd "${tmpdir}"

git status
git add .
git commit --author="Travis <ci@travis>" -m "new publish from travis ci ${TRAVIS_BUILD_NUMBER} ${TRAVIS_BUILD_WEB_URL}"
GIT_SSH_COMMAND="ssh -v -i $rsa" git push

popd


popd
