#!/bin/sh

_EXAMPLE_DIR_=`dirname "$0"`

cd ${_EXAMPLE_DIR_}

_OPTION_=$1

if [ "${_OPTION_}" = "" ]
then
  echo
  echo "Dynamic service examples"
  echo "------------------------"
  echo
  echo "1) Broken lookup (dangling field)"
  echo "2) Broken lookup (one-time lookup)"
  echo "3) Broken lookup (race condition)"
  echo "4) Correct lookup"
  echo "5) Broken listener"
  echo "6) Correct listener"
  echo "7) Standard tracker"
  echo "8) Customized tracker"
  echo "0) exit"
  echo

  read -p "Choose an example (1-8): " _OPTION_

  echo
fi

if [ "${_OPTION_}" = "0" ]
then
  exit
fi

ant "build_${_OPTION_}"

if [ "${?}" != "0" ]
then
  exit
fi

echo
echo "****************************"
echo "*                          *"
echo "* Launching OSGi container *"
echo "*                          *"
echo "****************************"
echo

java -jar launcher.jar bundles

