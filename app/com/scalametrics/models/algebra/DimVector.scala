package com.scalametrics.models.algebra

 sealed trait DimVector[A]
 final case class Vector1D[A](a: A) extends DimVector[A]
 final case class Vector2D[A](a: A, b: A) extends DimVector[A]
 final case class Vector3D[A](a: A, b: A, c: A) extends DimVector[A]

 object DimVector {
	 implicit def dimVecSemigroup[A : Semigroup]: Semigroup[DimVector[A]] = new ApplicativeSemigroup[A, DimVector]
   implicit def dimVecMonoid[A : Monoid]: Monoid[DimVector[A]] = new ApplicativeMonoid[A, DimVector]
	 implicit def dimVecGroup[A : Group]: Group[DimVector[A]] = new ApplicativeGroup[A, DimVector]

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
