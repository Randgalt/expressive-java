/*
To run this program: scala run scala3interpreter.scala
*/

//> using scala 3.6.4

import scala.annotation.tailrec

case class M[A](value: A):
  def bind[B](k: A => M[B]): M[B] = k(value)
  def map[B](f: A => B): M[B] = bind(x => unitM(f(x)))
  def flatMap[B](f: A => M[B]): M[B] = bind(f)

def unitM[A](a: A): M[A] = M(a)

def showM(m: M[Value]): String = m.value.toString

type Name = String

enum Term:
  case Var(x: Name)
  case Con(n: Int)
  case Add(l: Term, r: Term)
  case Lam(x: Name, body: Term)
  case App(fun: Term, arg: Term)

enum Value(asString: String):
  override def toString: String = asString

  case Wrong extends Value("wrong")
  case Num(n: Int) extends Value(n.toString)
  case Fun(f: Value => M[Value]) extends Value("<function>")

type Pair[+T1, +T2] = (T1, T2)
type Environment = List[Pair[Name, Value]]

import Term.*
import Value.*

@tailrec
def lookup(x: Name, e: Environment): M[Value] = e match
  case List() => unitM(Wrong)
  case Tuple2(y, b) :: e1 => if (x == y) unitM(b) else lookup(x, e1)

def add(a: Value, b: Value): M[Value] = Tuple2(a, b) match
  case Tuple2(Num(m), Num(n)) => unitM(Num(m + n))
  case _ => unitM(Wrong)

def apply(a: Value, b: Value): M[Value] = a match
  case Fun(k) => k(b)
  case _ => unitM(Wrong)

def interp(t: Term, e: Environment): M[Value] = t match
  case Var(x) => lookup(x, e)
  case Con(n) => unitM(Num(n))
  case Add(l, r) =>
    for (a <- interp(l, e);
         b <- interp(r, e);
         c <- add(a, b))
    yield c
  case Lam(x, t) => unitM(Fun(a => interp(t, (x, a) :: e)))
  case App(f, t) =>
    for (a <- interp(f, e);
         b <- interp(t, e);
         c <- apply(a, b))
    yield c

def test(t: Term): String =
  showM(interp(t, List()))

val term0: App = App(Lam("x", Add(Var("x"), Var("x"))), Add(Con(10), Con(11)))
val term1: App = App(Con(1), Con(2))

@main
def testInterpreter(): Unit =
  println(test(term0))
  println(test(term1))
