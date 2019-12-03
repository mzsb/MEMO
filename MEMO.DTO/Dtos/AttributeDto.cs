using MEMO.DTO.Enums;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace MEMO.DTO
{
    public class AttributeDto
    {
        public Guid Id { get; set; }

        [Required(ErrorMessage = "Attribute userId is required", AllowEmptyStrings = false)]
        public Guid UserId { get; set; }

        public UserDto User { get; set; }

        [Required(ErrorMessage = "Attribute name is required", AllowEmptyStrings = false), MaxLength(20)]
        public string Name { get; set; }
        public AttributeType Type { get; set; }

        public int AttributeValuesCount { get; set; }

        public ICollection<AttributeParameterDto> attributeParameters { get; set; } = new List<AttributeParameterDto>();
    }
}
