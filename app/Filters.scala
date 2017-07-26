import javax.inject.Inject

import play.api.http.HttpFilters
import play.api.mvc.EssentialFilter
import services.AuthFilter

class Filters @Inject()(authFilter: AuthFilter) extends HttpFilters {
  override def filters: Seq[EssentialFilter] = Seq(authFilter)
}
