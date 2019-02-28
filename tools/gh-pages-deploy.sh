#!/usr/bin/env bash

set -xe

pushd "${0%/*}"

tmpdir=tmp

rm -rf "$tmpdir"
mkdir "$tmpdir"

GIT_SSH_COMMAND='ssh -i secrets/github-pages_id_rsa' git clone -b gh-pages https://github.com/${TRAVIS_REPO_SLUG} "$tmpdir"

rm -rf "${tmpdir}/public"
cp -a ./public "${tmpdir}/public"

pushd "${tmpdir}/public"

git add .
git commit --author="ci@travis" -m "new publish from travis ci ${TRAVIS_BUILD_NUMBER} ${TRAVIS_BUILD_WEB_URL}"
GIT_SSH_COMMAND='ssh -i secrets/github-pages_id_rsa' git push

popd


popd
