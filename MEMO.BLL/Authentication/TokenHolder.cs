using System;
using System.Collections.Generic;
using System.Text;

namespace MEMO.BLL.Authentication
{
    public class TokenHolder
    {
        public Guid UserId { get; set; }

        public string Token { get; set; }
    }
}
