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

        public bool authorizeByUserId(Guid userId, string token) 
        {
            return userId.ToString() == _tokenManager.DecodeUserId(token) ||
                                        _tokenManager.DecodeUserRole(token) == "Administrator";
        }
    }
}
