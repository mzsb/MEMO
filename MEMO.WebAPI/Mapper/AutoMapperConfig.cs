using AutoMapper;
using MEMO.DAL.Entities;
using MEMO.WebAPI.Dtos;
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
                cfg.CreateMap<UserDto, User>();
                cfg.CreateMap<User, UserDto>().ForMember(dto => dto.Password, opt => opt.Ignore());
            });

            return config.CreateMapper();
        }
    }
}
