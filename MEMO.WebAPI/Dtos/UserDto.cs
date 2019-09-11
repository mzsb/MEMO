using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Threading.Tasks;

namespace MEMO.WebAPI.Dtos
{
    public class UserDto
    {
        public Guid Id { get; set; }

        [Required(ErrorMessage = "Username is required", AllowEmptyStrings = false)]
        public string UserName { get; set; }

        [Required(ErrorMessage = "Email is required", AllowEmptyStrings = false)]
        public string Email { get; set; }

        [Required(ErrorMessage = "Password is required", AllowEmptyStrings = false)]
        public string Password { get; set; }

        public string Token { get; set; }

        public string Role { get; set; }
    }
}
