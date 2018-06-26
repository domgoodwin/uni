const chai = require('chai');
const expect = chai.expect;
const request = require('superagent');
const status = require('http-status');


describe('Basic server tests', function(){
    var server;

    before(function(done){
        
    })

    it('GET Hello world', function(done){
        request.get('http://localhost:3000')
            .end(
                function(err, res){
                    expect(err).to.not.be.an('error');
                    expect(res.status).to.equal(status.OK);
                    expect(res.text).to.equal('Hello, World!');
                    done(); 
                }
            );
    });
    it("POST Request fails", function(done){
        request.post('http://localhost:3000')
            .end(function(err, res){
                expect(err).to.be.an("error");
                expect(res.status).to.equal(status.METHOD_NOT_ALLOWED);
                done();
            });
    });
});