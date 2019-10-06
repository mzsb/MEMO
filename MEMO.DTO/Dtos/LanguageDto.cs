using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace MEMO.DTO
{
    public class LanguageDto : DtoBase
    {
        [Required(ErrorMessage = "Language code is required", AllowEmptyStrings = false), MaxLength(2)]
        public string Code { get; set; }

        [Required(ErrorMessage = "Language code is required", AllowEmptyStrings = false)]
        public string Name { get; set; }

        public ICollection<DictionaryDto> Dictionaries { get; set; } = new List<DictionaryDto>();
    }
}
