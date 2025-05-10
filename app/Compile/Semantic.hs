module Compile.Semantic
  ( semanticAnalysis
  ) where

import           Compile.AST (AST(..), Expr(..), Stmt(..), posPretty)
import           Compile.Parser (parseNumber)
import           Error (L1ExceptT, semanticFail)

import           Control.Monad (unless, when)
import           Control.Monad.State
import qualified Data.Map as Map

data VariableStatus
  = Declared
  | Initialized
  deriving (Show)

-- You might want to keep track of some location information as well at some point
type Namespace = Map.Map String VariableStatus

type L1Semantic = StateT Namespace L1ExceptT

-- A little wrapper so we don't have to ($ lift) everywhere inside the StateT
semanticFail' :: String -> L1Semantic a
semanticFail' = lift . semanticFail

semanticAnalysis :: AST -> L1ExceptT ()
semanticAnalysis ast = do
  ns <- varStatusAnalysis ast
  evalStateT (checkReturns ast) ns

-- right now an AST is just a list of statements
varStatusAnalysis :: AST -> L1ExceptT Namespace
varStatusAnalysis (Block stmts _) = do
  execStateT (mapM_ checkStmt stmts) Map.empty

-- So far this checks:
-- + we cannot declare a variable again that has already been declared or initialized
-- + we cannot initialize a variable again that has already been declared or initialized
-- + a variable needs to be declared or initialized before we can assign to it
-- + we can only return valid expressions
checkStmt :: Stmt -> L1Semantic ()
checkStmt (Decl name pos) = do
  ns <- get
  let isDeclared = Map.member name ns
  when isDeclared
    $ semanticFail'
    $ "Variable " ++ name ++ " redeclared at: " ++ posPretty pos
  put $ Map.insert name Declared ns
checkStmt (Init name e pos) = do
  ns <- get
  let isDeclared = Map.member name ns
  when isDeclared
    $ semanticFail'
    $ "Variable " ++ name ++ " redeclared (initialized) at: " ++ posPretty pos
  checkExpr e
  put $ Map.insert name Initialized ns
checkStmt (Asgn name op e pos) = do
  ns <- get
  case op of
    Nothing -> do
      -- Assignment with `=`
      -- If we assign to a variable with `=`, it has to be either declared or initialized
      unless (Map.member name ns)
        $ semanticFail'
        $ "Trying to assign to undeclared variable "
            ++ name
            ++ " at: "
            ++ posPretty pos
      checkExpr e
      put $ Map.insert name Initialized ns
    Just _
    -- Assinging with op, e.g. `x += 3`,
    -- for this x needs to be intialized, not just declasred
     ->
      case Map.lookup name ns of
        Just Initialized -> do
          checkExpr e
        _ ->
          semanticFail'
            $ "Trying to assignOp to undeclared variable "
                ++ name
                ++ " at: "
                ++ posPretty pos


checkStmt (Ret e _) = checkExpr e

checkExpr :: Expr -> L1Semantic ()
checkExpr (IntExpr str pos) = do
  -- Check that literals are in bounds
  let res = parseNumber str
  case res of
    Left e -> do
      semanticFail' $ "Error in " ++ posPretty pos ++ e
    Right _ -> return ()
checkExpr (Ident name pos) = do
  ns <- get
  case Map.lookup name ns of
    Just Initialized -> return ()
    _ ->
      semanticFail'
        $ "Variable "
            ++ name
            ++ " used without initialization at: "
            ++ posPretty pos
checkExpr (UnExpr _ e) = checkExpr e
checkExpr (BinExpr _ lhs rhs) = checkExpr lhs >> checkExpr rhs

checkReturns :: AST -> L1Semantic ()
checkReturns (Block stmts _) = do
  let returns = any isReturn stmts
  unless returns $ semanticFail' "Program does not return"
  where
    isReturn (Ret _ _) = True
    isReturn _ = False
