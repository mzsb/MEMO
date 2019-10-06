using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace MEMO.DTO
{
    public class DictionaryDto : DtoBase
    {
        [Required(ErrorMessage = "Dictionary name is required", AllowEmptyStrings = false)]
        public string Name { get; set; }

        [Required(ErrorMessage = "Dictionary visibility is required")]
        public bool IsPublic { get; set; }

        [Required(ErrorMessage = "Dictionary owner user is required")]
        public UserDto Owner { get; set; }

        [Required(ErrorMessage = "Dictionary source language is required")]
        public LanguageDto Source { get; set; }

        [Required(ErrorMessage = "Dictionary destination language is required")]
        public LanguageDto Destination { get; set; }

        public ICollection<TranslationDto> Translations { get; set; } = new List<TranslationDto>();
        public ICollection<MetaDefinitionDto> MetaDefinitions { get; set; } = new List<MetaDefinitionDto>();
        public ICollection<UserDto> Viewers { get; set; } = new List<UserDto>();
    }
}
