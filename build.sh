#! /bin/bash

echo ' ___       ___  ___  ___       ________  ___  ________     '
echo '|\  \     |\  \|\  \|\  \     |\_____  \|\  \|\   __  \    '
echo '\ \  \    \ \  \\\  \ \  \     \|___/  /\ \  \ \  \|\  \   '
echo ' \ \  \    \ \  \\\  \ \  \        /  / /\ \  \ \  \\\  \  '
echo '  \ \  \____\ \  \\\  \ \  \____  /  /_/__\ \  \ \  \\\  \ '
echo '   \ \_______\ \_______\ \_______\\________\ \__\ \_______\'
echo '    \|_______|\|_______|\|_______|\|_______|\|__|\|_______|'
echo ''
echo '                                      The Greatest IO Monad'
echo ''

if [ ! -f ./compiler/lulzio-compiler ]; then
  if [ -x $(which ghc) ]; then
    echo "Building LULZIO compiler."
    ./compiler/bootstrap
    ./build.sh
  else
    echo "No GHC installation found. Proceeding with SLOW, UNOPTIMIZED compiler."
    echo "You really should consider using a more principled build system."
    echo "I would strongly enocourage you to install the Glasgow Haskell Compiler;"
    echo "Your quality of life will drastically improve, and you'll be able to"
    echo "enjoy OPTIMIZED LULZIO performance."
    sbt compile
  fi
else
  ./compiler/lulzio-compiler
  if [ $? -ne 0 ]; then
    rm ./compiler/lulzio-compile
    ./build.sh
  fi
fi
