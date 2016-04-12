package  com.scalametrics.models.algebra

trait Applicative[M[_]] extends Functor[M] {
	def pure[A](v: A): M[A]
	def ap[A, B](a: M[A])(f: M[A => B]): M[B]
	def splat[A, B, C](a: M[A], b: M[B])(f: (A, B) => C): M[C] = ap(b)(ap(a)(pure(f.curried)))
}
object Applicative {
	def apply[M[_] : Applicative]: Applicative[M] = implicitly
	def pure[A, M[_] : Applicative](v: A): M[A] = implicitly[Applicative[M]].pure(v)
	def ap[M[_] : Applicative, A, B](a: M[A])(f: M[A => B]): M[B] = implicitly[Applicative[M]].ap(a)(f)
	def splat[M[_], A, B, C](a: M[A], b: M[B])(f: (A, B) => C)(implicit applic: Applicative[M]): M[C] = {
		applic.ap(b)(applic.ap(a)(applic.pure(f.curried)))
	}

	  implicit val listApplicative: Applicative[List] = new Applicative[List] {
		def pure[A](v: A): List[A] = List(v)
		def fmap[A, B](l: List[A])(f: A => B): List[B] = {
			l.map(f(_))
		}
		def ap[A, B](l: List[A])(f: List[A => B]): List[B] = {
			l.flatMap { a =>
				f.map(b => b(a))
				}
			}
		}
		implicit val optionApplicative = new Applicative[Option] {
			def pure[A](v: A): Option[A] = v match {
				case Some(_) => Option(v)
				case None => None
			}
			def fmap[A, B](o: Option[A])(f: A => B): Option[B] = o match {
				case Some(_) => Option(f(o.get))
				case None => None
			}
			def ap[A, B](o: Option[A])(f: Option[A => B]): Option[B] = o match {
				case Some(_) => Option(f.get(o.get))
				case None => None
			}
		}
		implicit class ApplicativeOps[A, M[_] : Applicative](a: M[A]) {
			def pure(v: A): M[A] = implicitly[Applicative[M]].pure(v)
			def ap[B](f: M[A => B]): M[B] = implicitly[Applicative[M]].ap(a)(f)
			def splat[B, C](b: M[B])(f: (A, B) => C)(implicit applic: Applicative[M]) = {
				applic.ap(b)(applic.ap(a)(applic.pure(f.curried)))
			}
		}
	}

