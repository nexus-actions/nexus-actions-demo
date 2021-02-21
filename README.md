# create-nexus-staging-repository-sample

A Kotlin multiplatform sample project that uses the [create-nexus-staging-repo](https://github.com/martinbonnin/create-nexus-staging-repo) Github Action to create a Nexus repository ahead of upload and avoid split staging repositories.

The `create-nexus-staging-repository` will create a repository in a first job that the other jobs will depend on.

```yaml
jobs:
  create_staging_repository:
    runs-on: ubuntu-latest
    name: Create staging repository
    outputs:
      # connect the step output to the job output
      repository-id: ${{ steps.create.outputs.repository-id }}
    steps:
    - id: create
      uses: martinbonnin/create-nexus-staging-repo@main
      with:
        # The username you use to connect to Sonatype's Jira
        # Do not use secrets for the username since the repository id will contain it
        # and it will not be passed across jobs :/
        username: mbonnin
        password: ${{ secrets.SONATYPE_PASSWORD }}
        # Your staging profile ID. You can get it at https://oss.sonatype.org/#stagingProfiles;$staginProfileId
        staging-profile-id: ${{ secrets.SONATYPE_STAGING_PROFILE_ID }}
        # a description to identify your repository in the UI
        description: Created by $GITHUB_WORKFLOW ($GITHUB_ACTION) for $GITHUB_REPOSITORY
```


