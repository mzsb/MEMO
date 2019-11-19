using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace MEMO.WebAPI.Controllers
{
    [AllowAnonymous]
    [Route("api/[Controller]")]
    public class ConnectionController : ControllerBase
    {
        public class Connection { public string Ip { get; set; } }

        [HttpGet]
        public ActionResult<Connection> Connect()
        {
            return new Connection { Ip = HttpContext.Connection.LocalIpAddress.ToString() };
        }
    }
}
