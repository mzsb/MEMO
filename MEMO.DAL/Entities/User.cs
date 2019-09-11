using Microsoft.AspNetCore.Identity;
using System;
using System.ComponentModel.DataAnnotations.Schema;

namespace MEMO.DAL.Entities
{
    public class User : IdentityUser<Guid>
    {
        [NotMapped]
        public string Password { get; set; }

        [NotMapped]
        public string Token { get; set; }

        [NotMapped]
        public string Role { get; set; }
    }
}
