#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    openssl aes-256-cbc -K $encrypted_653259e5b3a8_key -iv $encrypted_653259e5b3a8_iv -in ci/codesigning.asc.enc -out ci/codesigning.asc -d
    gpg --fast-import ci/codesigning.asc
fi
