using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace MEMO.BLL.Authentication
{
    public class Login
    {
        [MaxLength(15)]
        public string UserName { get; set; }

        [MaxLength(20)]
        public string Password { get; set; }
    }
}
