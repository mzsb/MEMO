using MEMO.DTO.Interfaces;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace MEMO.DTO
{
    public class UserDto : IAuditable
    {
        public Guid Id { get; set; }

        [Required(ErrorMessage = "Username is required", AllowEmptyStrings = false), MaxLength(15)]
        public string UserName { get; set; }

        [Required(ErrorMessage = "Email is required", AllowEmptyStrings = false), MaxLength(50)]
        public string Email { get; set; }

        [Required(ErrorMessage = "Role is required", AllowEmptyStrings = false)]
        public string Role { get; set; }

        public int DictionaryCount { get; set; }
        public int ViewedDictionaryCount { get; set; }
        public int TranslationCount { get; set; }

        public ICollection<AttributeDto> Attributes { get; set; } = new List<AttributeDto>();

        public ICollection<DictionaryDto> Dictionaries { get; set; } = new List<DictionaryDto>();
        public String CreationDate { get; set; }
    }
}
