using MEMO.BLL.Authentication;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace MEMO.BLL.Interfaces
{
    public interface IAuthenticationService
    {
        Task<TokenHolder> LoginAsync(Login login);
        Task<TokenHolder> AutoLoginAsync(string token);
        Task<TokenHolder> RegistrationAsync(Registration registration);
        Task<TokenHolder> RefreshTokenAsync(string token);
    }
}
