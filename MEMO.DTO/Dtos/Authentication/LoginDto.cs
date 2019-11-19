using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace MEMO.DTO
{
    public class LoginDto
    {
        [Required(ErrorMessage = "Username is required", AllowEmptyStrings = false), MaxLength(15)]
        public string UserName { get; set; }

        [Required(ErrorMessage = "Password is required", AllowEmptyStrings = false), MaxLength(20)]
        public string Password { get; set; }
    }
}
