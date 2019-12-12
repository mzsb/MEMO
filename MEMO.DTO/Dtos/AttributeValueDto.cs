using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace MEMO.DTO
{
    public class AttributeValueDto
    {
        public Guid Id { get; set; }

        [Required(ErrorMessage = "AttributeValue value is required", AllowEmptyStrings = false), MaxLength(70)]
        public string Value { get; set; }

        [Required(ErrorMessage = "AttributeValue attributeId is required")]
        public Guid AttributeId { get; set; }

        public AttributeDto Attribute { get; set; }
    }
}
