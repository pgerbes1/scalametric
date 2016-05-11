package com.scalametrics.models.algebra

 sealed trait DimVector[A]
 final case class Vector1D[A](a: A) extends DimVector[A]
 final case class Vector2D[A](a: A, b: A) extends DimVector[A]
 final case class Vector3D[A](a: A, b: A, c: A) extends DimVector[A]

 object DimVector {
	 import VectorSpace._

	 implicit def dimVecSemigroup[A : Semigroup]: Semigroup[DimVector[A]] = new ApplicativeSemigroup[A, DimVector]
   implicit def dimVecMonoid[A : Monoid]: Monoid[DimVector[A]] = new ApplicativeMonoid[A, DimVector]
	 implicit def dimVecGroup[A : Group]: Group[DimVector[A]] = new ApplicativeGroup[A, DimVector]
	 implicit def dimVecRing[A : Ring]: Ring[DimVector[A]] = new ApplicativeRing[A, DimVector]

	 implicit def dimVectorSpace[A : Field] = from[A, DimVector] {
		 (s, vec) => dimVecApplicative.fmap(vec)(scale => Field.multiply(s, scale))
	 }

	 implicit val dimVecFoldable: Foldable[DimVector] = new Foldable[DimVector] {
		 override def foldLeft[A, B](fa: DimVector[A])(init: B)(f: (B, A) => B): B =
			 fa match {
				 case Vector1D(a) => f(init, a)
				 case Vector2D(a, b) => f(f(init, a), b)
				 case Vector3D(a, b, c) => f(f(f(init, a), b), c)
			 }

		 override def foldRight[A, B](fa: DimVector[A])(init: B)(f: (A, B) => B): B =
			 fa match {
				 case Vector1D(a) => f(a, init)
				 case Vector2D(a, b) => f(b, f(a, init))
				 case Vector3D(a, b, c) => f(c, f(b, f(a, init)))
			 }
	 }

	 implicit val dimVecApplicative: Applicative[DimVector] = new Applicative[DimVector] {
		def pure[A](a: A): DimVector[A] = Vector3D(a, a, a)

		def fmap[A, B](fa: DimVector[A])(f: A => B): DimVector[B] =
			fa match {
				case Vector1D(a) => Vector1D(f(a))
				case Vector2D(a, b) => Vector2D(f(a), f(b))
				case Vector3D(a, b, c) => Vector3D(f(a), f(b), f(c))
			}

		def <*>[A, B](fa: DimVector[A])(f: DimVector[A => B]): DimVector[B] =
			fa match {
				case Vector1D(a) =>
					f match {
						case Vector1D(g) => Vector1D(g(a))
						case Vector2D(g, _) => Vector1D(g(a))
						case Vector3D(g, _, _) => Vector1D(g(a))
					}
				case Vector2D(a, b) =>
					f match {
						case Vector1D(g) => Vector1D(g(a))
						case Vector2D(g, h) => Vector2D(g(a), h(b))
						case Vector3D(g, h, _) => Vector2D(g(a), h(b))
					}
				case Vector3D(a, b, c) =>
					f match {
						case Vector1D(g) => Vector1D(g(a))
						case Vector2D(g, h) => Vector2D(g(a), h(b))
						case Vector3D(g, h, i) => Vector3D(g(a), h(b), i(c))
					}
			}
	  }
	}
