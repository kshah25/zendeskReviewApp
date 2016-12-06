Add in agent photos in src/main/res/drawables folder.

Photos should be in this format: fname_l

Where 'fname' is the full first name of the agent as it appears on Zendesk, and
'l' is the initial of the last name. All filenames should be lowercase.


If you choose to use your own API key, you will need to modify the following in strings.xml:
server_env, which is the zendesk URL for your company
auth_email, the email address associated with the API token
api_key, the API key
