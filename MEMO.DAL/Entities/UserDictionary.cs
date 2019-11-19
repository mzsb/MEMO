using MEMO.DAL.Enums;
using System;
using System.Collections.Generic;
using System.Text;

namespace MEMO.DAL.Entities
{
    public class UserDictionary
    {
        public Guid Id { get; set; }
        public UserType Type { get; set; }

        public Guid DictionaryId { get; set; }
        public Dictionary Dictionary { get; set; }

        public Guid UserId { get; set; }
        public User User { get; set; }
    }
}
