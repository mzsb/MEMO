using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace MEMO.DTO
{
    public class UserDto : DtoBase
    {
        [Required(ErrorMessage = "Username is required", AllowEmptyStrings = false)]
        public string UserName { get; set; }

        [Required(ErrorMessage = "Email is required", AllowEmptyStrings = false)]
        public string Email { get; set; }

        [Required(ErrorMessage = "Role is required", AllowEmptyStrings = false)]
        public string Role { get; set; }

        public ICollection<DictionaryDto> Dictionaries { get; set; } = new List<DictionaryDto>();
    }
}
