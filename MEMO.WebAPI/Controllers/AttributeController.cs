using AutoMapper;
using MEMO.BLL.Interfaces;
using MEMO.DAL.Entities;
using MEMO.DTO;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace MEMO.WebAPI.Controllers
{
    [Route("api/[Controller]")]
    public class AttributeController : ControllerBase
    {
        private readonly IAttributeService _service;
        private readonly IMapper _mapper;

        public AttributeController(IAttributeService service,
                                   IMapper mapper)
        {
            _service = service;
            _mapper = mapper;
        }

        [Authorize(Roles = "Administrator")]
        [HttpGet]
        public async Task<ActionResult<IEnumerable<AttributeDto>>> GetAsync()
        {
            return _mapper.Map<List<AttributeDto>>(await _service.GetAsync());
        }

        [Authorize(Roles = "Administrator,User")]
        [HttpGet("{id}", Name = "GetAttributeById")]
        public async Task<ActionResult<AttributeDto>> GetAttributeByIdAsync(System.Guid id)
        {
            return _mapper.Map<AttributeDto>(await _service.GetByIdAsync(id));
        }

        [Authorize(Roles = "Administrator,User")]
        [HttpPost]
        public async Task<ActionResult> InsertAsync([FromBody] AttributeDto attribute)
        {
            _service.Token = await getAccessToken();

            var inserted = await _service.InsertAsync(_mapper.Map<Attribute>(attribute));

            return CreatedAtAction("GetAttributeById", new { id = inserted.Id }, inserted); ;
        }

        [Authorize(Roles = "Administrator,User")]
        [HttpPut]
        public async Task<ActionResult> UpdateAsync([FromBody] AttributeDto attribute)
        {
            _service.Token = await getAccessToken();

            await _service.UpdateAsync(_mapper.Map<Attribute>(attribute));

            return Ok();
        }

        [Authorize(Roles = "Administrator,User")]
        [HttpDelete("{id}")]
        public async Task<ActionResult> DeleteAsync(System.Guid id)
        {
            _service.Token = await getAccessToken();

            await _service.DeleteAsync(id);

            return Ok();
        }

        [Authorize(Roles = "Administrator,User")]
        [HttpGet("user/{id}")]
        public async Task<ActionResult<IEnumerable<AttributeDto>>> GetByUserIdAsync(System.Guid id)
        {
            return _mapper.Map<List<AttributeDto>>(await _service.GetByUserIdAsync(id));
        }

        private async Task<string> getAccessToken() => await HttpContext.GetTokenAsync("access_token");
    }
}
