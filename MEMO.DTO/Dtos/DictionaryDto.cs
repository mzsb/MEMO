using MEMO.DTO.Interfaces;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace MEMO.DTO
{
    public class DictionaryDto : IAuditable
    {
        public Guid Id { get; set; }

        [Required(ErrorMessage = "Dictionary name is required", AllowEmptyStrings = false), MaxLength(20)]
        public string Name { get; set; }

        [MaxLength(250)]
        public string Description { get; set; }

        [Required(ErrorMessage = "Dictionary visibility is required")]
        public bool IsPublic { get; set; }

        [Required(ErrorMessage = "Dictionary fast accessibility is required")]
        public bool IsFastAccessible { get; set; }

        [Required(ErrorMessage = "Dictionary owner user is required")]
        public UserDto Owner { get; set; }

        [Required(ErrorMessage = "Dictionary source language is required")]
        public LanguageDto Source { get; set; }

        [Required(ErrorMessage = "Dictionary destination language is required")]
        public LanguageDto Destination { get; set; }

        public int TranslationCount { get; set; }

        public int ViewerCount { get; set; }

        public ICollection<TranslationDto> Translations { get; set; } = new List<TranslationDto>();
        public ICollection<UserDto> Viewers { get; set; } = new List<UserDto>();
        public string CreationDate { get; set; }
    }
}
