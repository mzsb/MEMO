using Microsoft.AspNetCore.Identity;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations.Schema;

namespace MEMO.DAL.Entities
{
    public class User : IdentityUser<Guid>
    {
        [NotMapped]
        public string Role { get; set; }

        public ICollection<UserDictionary> UserDictionaries { get; set; } = new List<UserDictionary>();
    }
}
