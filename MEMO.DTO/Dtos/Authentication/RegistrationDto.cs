using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace MEMO.DTO
{
    public class RegistrationDto
    {
        [Required(ErrorMessage = "Username is required", AllowEmptyStrings = false)]
        public string UserName { get; set; }

        [Required(ErrorMessage = "Email is required", AllowEmptyStrings = false)]
        public string Email { get; set; }

        [Required(ErrorMessage = "Password is required", AllowEmptyStrings = false)]
        public string Password { get; set; }
    }
}
