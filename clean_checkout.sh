#!/bin/sh

cd mckinley
git checkout .
git pull -r
git checkout $1 
