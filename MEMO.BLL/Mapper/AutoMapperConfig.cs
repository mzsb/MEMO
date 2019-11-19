using AutoMapper;
using MEMO.BLL.Authentication;
using MEMO.DAL.Entities;
using MEMO.DTO;
using MEMO.DAL.Enums;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace MEMO.BLL.Mapper
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
                cfg.CreateMap<LanguageDto, Language>().ReverseMap();
                cfg.CreateMap<TranslationDto, Translation>().ReverseMap();
                cfg.CreateMap<AttributeValueDto, AttributeValue>().ReverseMap();
                cfg.CreateMap<AttributeDto, Attribute>().ReverseMap();
                cfg.CreateMap<AttributeParameterDto, AttributeParameter>().ReverseMap();
                #region Dictionary

                cfg.CreateMap<Dictionary, DictionaryDto>()
                   .ForMember(dto => dto.Destination, opt => opt.Ignore())
                   .ForMember(dto => dto.Source, opt => opt.Ignore())
                   .ForMember(dto => dto.Owner, opt => opt.Ignore())
                   .ForMember(dto => dto.Viewers, opt => opt.Ignore())
                   .AfterMap((d, dto, ctx) =>
                   {

                        dto.Source = ctx.Mapper.Map<LanguageDto>(
                            d.DictionaryLanguages
                             .Where(dl => dl.Type.Equals(LanguageType.source))
                             .Select(dl => dl.Language)
                             .SingleOrDefault());

                        dto.Destination = ctx.Mapper.Map<LanguageDto>(
                            d.DictionaryLanguages
                             .Where(dl => dl.Type.Equals(LanguageType.destination))
                             .Select(dl => dl.Language)
                             .SingleOrDefault());

                        dto.Owner = ctx.Mapper.Map<UserDto>(
                            d.UserDictionaries
                             .Where(dl => dl.Type == UserType.owner)
                             .Select(dl => dl.User)
                             .SingleOrDefault());

                        foreach (var viewer in d.UserDictionaries
                                                .Where(ud => ud.Type == UserType.viewer)
                                                .Select(ud => ud.User))
                        {
                            dto.Viewers.Add(ctx.Mapper.Map<UserDto>(viewer));
                        }

                   });

                cfg.CreateMap<DictionaryDto, Dictionary>()
                   .ForMember(d => d.DictionaryLanguages, opt => opt.Ignore())
                   .ForMember(d => d.UserDictionaries, opt => opt.Ignore())
                   .AfterMap((dto, d, ctx) => {

                        d.DictionaryLanguages = new List<DictionaryLanguage> {
                           new DictionaryLanguage
                           {
                               LanguageId = dto.Destination.Id,
                               Type = LanguageType.destination
                           },                            new DictionaryLanguage
                            {
                                LanguageId = dto.Source.Id,
                                Type = LanguageType.source
                            }
                           };



                        d.UserDictionaries = new List<UserDictionary> {
                            new UserDictionary
                            {
                                User = ctx.Mapper.Map<User>(dto.Owner),
                                Type = UserType.owner
                            }};

                        foreach (var viewer in dto.Viewers)
                        {
                            d.UserDictionaries.Add(
                                new UserDictionary
                                {
                                    User = ctx.Mapper.Map<User>(viewer),
                                    Type = UserType.viewer
                                } );
                        }

                   });

                #endregion
            });

            return config.CreateMapper();
        }
    }
}
