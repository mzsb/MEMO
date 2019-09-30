using AutoMapper;
using MEMO.BLL.Authentication;
using MEMO.DAL.Entities;
using MEMO.DTO;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace MEMO.WebAPI.Mapper
{
    public class AutoMapperConfig
    {
        public static IMapper Configure()
        {
            var config = new MapperConfiguration(cfg =>
            {
                cfg.CreateMap<LoginDto, Login>().ReverseMap();
                cfg.CreateMap<RegistrationDto, Registration>().ReverseMap();
                cfg.CreateMap<TokenHolderDto, TokenHolder>().ReverseMap();

                cfg.CreateMap<UserDto, User>().ReverseMap();
            });

            return config.CreateMapper();
        }
    }
}
