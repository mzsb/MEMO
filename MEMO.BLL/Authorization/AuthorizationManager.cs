using MEMO.BLL.Authentication;
using System;
using System.Collections.Generic;
using System.Text;

namespace MEMO.BLL.Authorization
{
    public class AuthorizationManager
    {
        private readonly TokenManager _tokenManager;

        public AuthorizationManager(TokenManager tokenManager)
        {
            _tokenManager = tokenManager;
        }

        public bool AuthorizeByUserId(Guid userId, string token) 
        {
            return _tokenManager.DecodeUserId(token) == userId.ToString() ||
                   _tokenManager.DecodeUserRole(token) == "Administrator";
        }
    }
}
