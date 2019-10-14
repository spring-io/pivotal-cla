#!/bin/sh
mysql -uroot -ppassword -e 'create database pivotalcla;'
mysql -uroot -ppassword -e "create user 'spring' identified by 'password';"
mysql -uroot -ppassword -e "grant all on pivotalcla.* to 'spring'";