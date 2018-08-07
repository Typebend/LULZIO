module Main where

import System.Process

-- MUST BE COMPILED WITH -O3
-- OTHERWISE SUPEROPTIMIZATIONS FAIL
main::IO()
main = callCommand "cd .. && sbt compile"
