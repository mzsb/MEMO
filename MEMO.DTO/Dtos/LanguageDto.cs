using MEMO.DTO.Enums;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace MEMO.DTO
{
    public class LanguageDto
    {
        public Guid Id { get; set; }

        [Required(ErrorMessage = "Language code is required")]
        public LanguageCode LanguageCode { get; set; }

        public ICollection<DictionaryDto> Dictionaries { get; set; } = new List<DictionaryDto>();
    }
}
