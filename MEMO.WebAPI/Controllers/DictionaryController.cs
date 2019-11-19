using AutoMapper;
using MEMO.BLL.Interfaces;
using MEMO.DAL.Entities;
using MEMO.DTO;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;


namespace MEMO.WebAPI.Controllers
{
    [Route("api/[Controller]")]
    public class DictionaryController : ControllerBase
    {
        private readonly IDictionaryService _service;
        private readonly IMapper _mapper;

        public DictionaryController(IDictionaryService service,
                                    IMapper mapper)
        {
            _service = service;
            _mapper = mapper;
        }

        [Authorize(Roles = "Administrator")]
        [HttpGet]
        public async Task<ActionResult<IEnumerable<DictionaryDto>>> GetAsync()
        {
            return _mapper.Map<List<DictionaryDto>>(await _service.GetAsync());
        }

        [Authorize(Roles = "Administrator,User")]
        [HttpGet("{id}", Name = "GetDictionaryById")]
        public async Task<ActionResult<DictionaryDto>> GetDictionaryByIdAsync(Guid id)
        {
            return _mapper.Map<DictionaryDto>(await _service.GetByIdAsync(id));
        }

        [Authorize(Roles = "Administrator,User")]
        [HttpPost]
        public async Task<ActionResult> InsertAsync([FromBody] DictionaryDto dictionary)
        {
            _service.Token = await getAccessToken();

            var inserted = await _service.InsertAsync(_mapper.Map<Dictionary>(dictionary));

            return CreatedAtAction("GetDictionaryById", new { id = inserted.Id }, inserted); ;
        }

        [Authorize(Roles = "Administrator,User")]
        [HttpPut]
        public async Task<ActionResult> UpdateAsync([FromBody] DictionaryDto dictionary)
        {
            _service.Token = await getAccessToken();

            await _service.UpdateAsync(_mapper.Map<Dictionary>(dictionary));

            return Ok();
        }

        [Authorize(Roles = "Administrator,User")]
        [HttpDelete("{id}")]
        public async Task<ActionResult> DeleteAsync(Guid id)
        {
            _service.Token = await getAccessToken();

            await _service.DeleteAsync(id);

            return Ok();
        }

        [Authorize(Roles = "Administrator,User")]
        [HttpGet("user/{id}")]
        public async Task<ActionResult<IEnumerable<DictionaryDto>>> GetByUserIdAsync(Guid id)
        {
            return _mapper.Map<List<DictionaryDto>>(await _service.GetByUserIdAsync(id));
        }

        [Authorize(Roles = "Administrator,User")]
        [HttpGet("public/{userId}")]
        public async Task<ActionResult<IEnumerable<DictionaryDto>>> GetPublicAsync(Guid userId)
        {
            return _mapper.Map<List<DictionaryDto>>(await _service.GetPublicAsync(userId));
        }

        [Authorize(Roles = "Administrator,User")]
        [HttpGet("fastaccessible/{id}")]
        public async Task<ActionResult<IEnumerable<DictionaryDto>>> GetFastAccessibleAsync(Guid id)
        {
            _service.Token = await getAccessToken();

            return _mapper.Map<List<DictionaryDto>>(await _service.GetFastAccessibleAsync(id));
        }

        [Authorize(Roles = "Administrator,User")]
        [HttpPost("subscribe/{userId}")]
        public async Task<ActionResult> SubscribeAsync(Guid userId, [FromBody] DictionaryDto dictionary)
        {
            _service.Token = await getAccessToken();

            await _service.SubscribeAsync(userId, _mapper.Map<Dictionary>(dictionary));

            return Ok();
        }

        [Authorize(Roles = "Administrator,User")]
        [HttpPost("unsubscribe/{userId}")]
        public async Task<ActionResult> UnsubscribeAsync(Guid userId, [FromBody] DictionaryDto dictionary)
        {
            _service.Token = await getAccessToken();

            await _service.UnsubscribeAsync(userId, _mapper.Map<Dictionary>(dictionary));

            return Ok();
        }

        private async Task<string> getAccessToken() => await HttpContext.GetTokenAsync("access_token");
    }
}
